import {User} from "@/model/User";
import {Post} from "@/model/Post";

export interface Comment {
    id?: string;
    content?: string;
    create_at?: Date;
    last_modify_at?: Date;
    user?: User
    post?: Post
}