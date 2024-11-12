import {User} from "@/model/User";
import {PostCategory} from "@/model/PostCategory";
import {PostFile} from "@/model/PostFile";
import {Comment} from "@/model/Comment"

export interface Post {
    id?: number;
    title?: string;
    content?: string;
    create_at?: string;
    last_modify_at?: string;
    user?: User;
    post_category?: PostCategory;
    post_files?: PostFile[];
    comments?: Comment[];
    comment_count?: number;
    read_at?: string;
}