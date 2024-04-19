package com.duu.ojquestionservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.duu.ojcommon.annotation.AuthCheck;
import com.duu.ojcommon.annotation.RequestLimit;
import com.duu.ojcommon.common.BaseResponse;
import com.duu.ojcommon.common.DeleteRequest;
import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.common.ResultUtils;
import com.duu.ojcommon.constant.UserConstant;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojcommon.exception.ThrowUtils;
import com.duu.ojmodel.model.dto.question.*;
import com.duu.ojmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.duu.ojmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.duu.ojmodel.model.entity.*;
import com.duu.ojmodel.model.vo.CommentVO;
import com.duu.ojmodel.model.vo.QuestionSubmitVO;
import com.duu.ojmodel.model.vo.QuestionVO;
import com.duu.ojquestionservice.dao.QuestionEsDao;
import com.duu.ojquestionservice.service.*;
import com.duu.ojserviceclient.service.UserFeignClient;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import static com.duu.ojcommon.constant.RedisKeyConstant.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CommentService commentService;

    @Resource
    private CommentReplyService commentReplyService;

    @Resource
    private CommentLikeService commentLikeService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private QuestionEsDao questionEsDao;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest,
                                          HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = userFeignClient.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        questionEsDao.save(QuestionEsDTO.objToDto(question));
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        //删除缓存
        stringRedisTemplate.delete(QUESTION_ID+id);
        //删除ES
        questionEsDao.delete(QuestionEsDTO.objToDto(oldQuestion));
        return ResultUtils.success(true);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, new BusinessException(ErrorCode.OPERATION_ERROR));
        //清除缓存
        stringRedisTemplate.delete(QUESTION_ID+id);
        //保存进Es
        questionEsDao.save(QuestionEsDTO.objToDto(question));
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getQuestionByRedis(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        // 不是本人或管理员，不能直接获取所有信息
        if (!question.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 根据 id 获取（脱敏）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getQuestionByRedis(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                           HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest,
                                              HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    @RequestLimit(period = 1, count = 1)
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userFeignClient.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userFeignClient.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

    @PostMapping("/progressCount")
    public BaseResponse<QuestionStatisticsResponse> questionStatistics(HttpServletRequest request){
        User loginUser = userFeignClient.getLoginUser(request);
        return ResultUtils.success(questionService.questionStatistics(loginUser.getId()));
    }
    @PostMapping("/comment")
    @RequestLimit(period = 5,count = 1)
    public BaseResponse<Long> questionComment(@RequestBody CommentAddRequest commentAddRequest,HttpServletRequest request){
        Long questionId = commentAddRequest.getQuestionId();
        String content = commentAddRequest.getContent();
        User loginUser = userFeignClient.getLoginUser(request);
        Comment comment = new Comment();
        comment.setQuestionId(questionId);
        comment.setUserId(loginUser.getId());
        comment.setContent(content);
        boolean save = commentService.save(comment);
        ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR,"评论失败");
        Long commentId = comment.getId();
        return ResultUtils.success(commentId);
    }
    @GetMapping("/comment/delete")
    public BaseResponse<Boolean> deleteComment(@RequestParam Long id,HttpServletRequest request){
        User loginUser = userFeignClient.getLoginUser(request);
        Comment comment = commentService.getById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if(!comment.getUserId().equals(loginUser.getId())&& !userFeignClient.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"用户只可删除自己的评论");
        }
        boolean removeById = commentService.removeById(id);
        ThrowUtils.throwIf(!removeById,ErrorCode.OPERATION_ERROR);
        QueryWrapper<CommentReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commentId",id);
        boolean remove = commentReplyService.remove(queryWrapper);
        ThrowUtils.throwIf(!remove,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    @PostMapping("/comment_reply")
    @RequestLimit(period = 5,count = 1)
    public BaseResponse<Long> addCommentReply(@RequestBody CommentReplyAddRequest commentReplyAddRequest,HttpServletRequest request){
        User loginUser = userFeignClient.getLoginUser(request);
        Long commentId = commentReplyAddRequest.getCommentId();
        String content = commentReplyAddRequest.getContent();
        CommentReply commentReply = new CommentReply();
        commentReply.setCommentId(commentId);
        commentReply.setUserId(loginUser.getId());
        commentReply.setContent(content);
        boolean save = commentReplyService.save(commentReply);
        ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR,"回复失败");
        Long commentReplyId = commentReply.getId();
        return ResultUtils.success(commentReplyId);
    }
    @GetMapping("/comment_reply/delete")
    public BaseResponse<Boolean> deleteCommentReply(@RequestParam Long id,HttpServletRequest request){
        User loginUser = userFeignClient.getLoginUser(request);
        CommentReply commentReply = commentReplyService.getById(id);
        if (commentReply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if(!commentReply.getUserId().equals(loginUser.getId())&& !userFeignClient.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"用户只可删除自己的回复");
        }
        boolean remove = commentReplyService.removeById(id);
        ThrowUtils.throwIf(!remove,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/comment/list/page")
    public BaseResponse<Page<CommentVO>> listQuestionCommentByPage(@RequestBody CommentQueryRequest commentQueryRequest,HttpServletRequest request){
        long current = commentQueryRequest.getCurrent();
        long size = commentQueryRequest.getPageSize();
        User loginUser = userFeignClient.getLoginUser(request);
        Page<Comment> commentPage = commentService.page(new Page<>(current, size), commentService.getQueryWrapper(commentQueryRequest));
        return ResultUtils.success(commentService.getCommentVOPage(commentPage,loginUser));
    }

    @GetMapping("/comment/like")
    public BaseResponse<Boolean> commentLike(@RequestParam Long id,HttpServletRequest request){
        User loginUser = userFeignClient.getLoginUser(request);
        return ResultUtils.success(commentLikeService.commentLike(id,loginUser));
    }
    @GetMapping("/comment/dislike")
    public BaseResponse<Boolean> commentDislike(@RequestParam Long id,HttpServletRequest request){
        User loginUser = userFeignClient.getLoginUser(request);
        return ResultUtils.success(commentLikeService.commentDislike(id,loginUser));
    }

    @PostMapping("/search")
    public BaseResponse<Page<QuestionVO>> SearchQuestionByEs(@RequestBody SearchQueryRequest searchQueryRequest){
        if (searchQueryRequest.getSearchText()==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"搜索内容不能为空");
        }
        return ResultUtils.success(questionService.searchQuestionByEs(searchQueryRequest));
    }
}
