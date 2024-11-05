import {Request} from "@/model/Request";
import {User} from "@/model/User";

export interface Order {
    requests: Request[]
    serial: string
    create_at: Date
    user?: User
}