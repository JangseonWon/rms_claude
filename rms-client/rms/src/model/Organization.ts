import {User} from "@/model/User";

export interface Organization {
    id?: string,
    name?: string
    type?: string
    registration_number?: string
    nursing_number?: string
    user?: User
}