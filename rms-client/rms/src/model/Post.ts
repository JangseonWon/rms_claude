import {PostComment} from "@/model/PostComment";
import {User} from "@/model/User";

export interface Post {
    id: string;
    title: string;
    content: string;
    create_at: string;
    last_modify_at: string;
    read: boolean;
    user?: User;
    post_category_id: string;
    files?: Files[];
    comments?: PostComment[];
}

interface Files {
    id: string;
    path: string;
    name: string;
    create_at: string;
}