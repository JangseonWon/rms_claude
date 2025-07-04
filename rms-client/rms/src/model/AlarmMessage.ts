import {Post} from "@/model/Post";

export interface AlarmMessage {
    user_id?: string;
    alarm_count?: number;
    post?: Post
}