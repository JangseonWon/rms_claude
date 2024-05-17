import {Sample} from "@/model/Sample";
import {Service} from "@/model/Service";

export interface Request {
    service?: Service
    order_id?: string
    service_id?: string
    user_service_id?: string
    status?: string
    memo?: string
    department?: string
    ward?: string
    physician?: string
    emp_id?: string
    emp_name?: string
    emp_mobile?: string
    test?: boolean
    credit?: boolean
    price?: number
    outsourcing_cost?: number
    sample?: Sample
    create_at?: Date
}