import {Service} from "@/model/Service";
import {Role} from "@/model/Role";

export interface User {
    id: string
    name?: string
    role?: Role
    type?: string
    email?: string
    state?: string
    phone_number?: string
    branch_serial?: string
    branch_name?: string
    services?: Service[]
}