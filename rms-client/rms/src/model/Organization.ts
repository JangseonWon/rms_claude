import {User} from "@/model/User";

export interface Organization {
    id: string,
    name: string
    user: User
}