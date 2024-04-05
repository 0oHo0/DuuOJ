/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { CommentReplyVO } from './CommentReplyVO';
import type { UserVO } from './UserVO';

export type CommentVO = {
    commentReplies?: Array<CommentReplyVO>;
    content?: string;
    createTime?: string;
    id?: number;
    questionId?: number;
    likeNum?: number;
    likeFlag?: boolean;
    userVO?: UserVO;
};
