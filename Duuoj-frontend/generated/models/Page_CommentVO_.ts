/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { CommentVO } from './CommentVO';
import type { OrderItem } from './OrderItem';

export type Page_CommentVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: Array<OrderItem>;
    pages?: number;
    records?: Array<CommentVO>;
    searchCount?: boolean;
    size?: number;
    total?: number;
};
