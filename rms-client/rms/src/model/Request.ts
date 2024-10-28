import {Sample} from "@/model/Sample";
import {Service} from "@/model/Service";
import {Report} from "@/model/Report"

export interface Request {
    service?: Service
    order_id?: string
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
    reports?: Report[]
    create_at?: Date
    cart_at?: Date
    complete_at?: Date
    resample_at?: Date
    reported_at?: Date
    serial?: string
}