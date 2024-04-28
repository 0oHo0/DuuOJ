package com.duu.ojquestionservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.constant.CommonConstant;
import com.duu.ojcommon.constant.RedisKeyConstant;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojcommon.exception.ThrowUtils;
import com.duu.ojcommon.utils.SqlUtils;
import com.duu.ojmodel.model.dto.question.QuestionEsDTO;
import com.duu.ojmodel.model.dto.question.QuestionQueryRequest;
import com.duu.ojmodel.model.dto.question.QuestionStatisticsResponse;
import com.duu.ojmodel.model.dto.question.SearchQueryRequest;
import com.duu.ojmodel.model.entity.Question;
import com.duu.ojmodel.model.entity.QuestionSubmit;
import com.duu.ojmodel.model.entity.User;
import com.duu.ojmodel.model.enums.JudgeInfoMessageEnum;
import com.duu.ojmodel.model.enums.QuestionSubmitStatusEnum;
import com.duu.ojmodel.model.vo.QuestionVO;
import com.duu.ojmodel.model.vo.UserVO;
import com.duu.ojquestionservice.mapper.QuestionMapper;
import com.duu.ojquestionservice.mapper.QuestionSubmitMapper;
import com.duu.ojquestionservice.service.QuestionService;
import com.duu.ojquestionservice.service.QuestionSubmitService;
import com.duu.ojserviceclient.service.UserFeignClient;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.duu.ojcommon.constant.RedisKeyConstant.GET_QUESTION_FILTER;
import static jodd.util.ThreadUtil.sleep;

/**
 * @author duu
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2023-08-07 20:58:00
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {
    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    RedissonClient redissonClient;
    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getUserById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(),
                questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        QueryWrapper<QuestionSubmit> submitQueryWrapper = new QueryWrapper<>();
        submitQueryWrapper.eq("status", QuestionSubmitStatusEnum.PASS.getValue());
        submitQueryWrapper.eq("userId", loginUser.getId());
        Set<Long> passQuestionIdSet = questionSubmitMapper.selectList(submitQueryWrapper).stream()
                .map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            if (passQuestionIdSet.contains(question.getId())){
                questionVO.setStatus(1); //已通过
            }
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userFeignClient.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public Question getQuestionByRedis(Long questionId) {
        String key = RedisKeyConstant.QUESTION_ID+questionId.toString();
        // 使用布隆过滤器解决缓存穿透
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(GET_QUESTION_FILTER);
        if(!filter.contains(questionId)){
            return null;
        }
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String jsonStr = operations.get(key);
        Question question;
        if (jsonStr==null||jsonStr.isEmpty()){
            // 加互斥锁 解决缓存击穿
            String lockKey = RedisKeyConstant.GET_QUESTION_LOCK+questionId.toString();
            Boolean setIfAbsent = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
            if (BooleanUtil.isTrue(setIfAbsent)) {
                question = this.getById(questionId);
                jsonStr = JSONUtil.toJsonStr(question);
                operations.set(key, jsonStr,1,TimeUnit.DAYS);
                stringRedisTemplate.delete(lockKey);
            }else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return getQuestionByRedis(questionId);
            }
        }
        question = JSONUtil.toBean(jsonStr, Question.class);
        return question;
    }

    @Override
    public Page<QuestionVO> searchQuestionByEs(SearchQueryRequest searchQueryRequest) {
        String searchText = searchQueryRequest.getSearchText();
        String type = searchQueryRequest.getType();
        List<String> tagList = searchQueryRequest.getTags();
        long current = searchQueryRequest.getCurrent();
        long pageSize = searchQueryRequest.getPageSize();
        String sortField = searchQueryRequest.getSortField();
        String sortOrder = searchQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete",0));
        if (!StringUtils.isNotBlank(type)){
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("title",searchText));
        }else{
            boolQueryBuilder.should(QueryBuilders.matchQuery(type,searchText));
        }
        boolQueryBuilder.minimumShouldMatch(1);
        if (CollectionUtils.isNotEmpty(tagList)){
            BoolQueryBuilder tagQueryBuilder = new BoolQueryBuilder();
            for (String tag : tagList) {
                tagQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
            }
            boolQueryBuilder.filter(tagQueryBuilder);
        }
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        Pageable pageable = PageRequest.of((int) current-1, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageable)
       //         .withSorts(sortBuilder)
                .build();
        SearchHits<QuestionEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, QuestionEsDTO.class);
        Page<QuestionVO> page = new Page<>(current,pageSize);
        page.setTotal(searchHits.getTotalHits());
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<QuestionEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> questionIdList = searchHitList.stream().map(searchHit->searchHit.getContent().getId())
                    .collect(Collectors.toList());
            List<Question> questionList = this.baseMapper.selectBatchIds(questionIdList);
            List<QuestionVO> questionVOList = questionList.stream().map(QuestionVO::objToVo).collect(Collectors.toList());
            page.setRecords(questionVOList);
        }
        return page;
    }

    @Override
    public QuestionStatisticsResponse questionStatistics(Long id) {
        String responseStr = stringRedisTemplate.opsForValue().get(RedisKeyConstant.QUESTION_STATISTICS_KEY+id);
        if (StringUtils.isNotBlank(responseStr)){
            return JSONUtil.toBean(responseStr, QuestionStatisticsResponse.class);
        }
        List<Question> toatalQuestionList = this.list();
        int simpleTotalNum = 0;
        int mediumTotalNum = 0;
        int hardTotalNum = 0;
        for (Question question : toatalQuestionList) {
            if (question.getTags().contains("简单")){
                simpleTotalNum++;
            }else if (question.getTags().contains("中等")){
                mediumTotalNum++;
            }else {
                hardTotalNum++;
            }
        }
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", id);
        queryWrapper.eq("status", QuestionSubmitStatusEnum.PASS.getValue());
        Set<Long> passQuestionIdSet = questionSubmitMapper.selectList(queryWrapper).stream().map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
        List<Question> passQuestionList = this.listByIds(passQuestionIdSet);
        QuestionStatisticsResponse response = new QuestionStatisticsResponse();
        int simpleAcceptedNum = 0;
        int mediumAcceptedNum = 0;
        int hardAcceptedNum = 0;
        for (Question question : passQuestionList) {
            if (question.getTags().contains("简单")){
                simpleAcceptedNum++;
            }else if (question.getTags().contains("中等")){
                mediumAcceptedNum++;
            }else {
                hardAcceptedNum++;
            }
        }
        response.setSimpleTotalNum(simpleTotalNum);
        response.setSimpleAcceptedNum(simpleAcceptedNum);
        response.setMediumTotalNum(mediumTotalNum);
        response.setMediumAcceptedNum(mediumAcceptedNum);
        response.setHardTotalNum(hardTotalNum);
        response.setHardAcceptedNum(hardAcceptedNum);
        response.setTotalNum(simpleTotalNum+mediumTotalNum+hardTotalNum);
        response.setAcceptedNum(simpleAcceptedNum+mediumAcceptedNum+hardAcceptedNum);
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.QUESTION_STATISTICS_KEY+id,JSONUtil.toJsonStr(response));
        stringRedisTemplate.expire(RedisKeyConstant.QUESTION_STATISTICS_KEY+id,15,TimeUnit.MINUTES);
        return response;
    }
}