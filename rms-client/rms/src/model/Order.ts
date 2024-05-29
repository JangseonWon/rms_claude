import {Request} from "@/model/Request";

export interface Order {
    requests: Request[]
    serial: string
    create_at: Date
}