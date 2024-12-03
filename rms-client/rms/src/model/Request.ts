import {Sample} from "@/model/Sample";
import {Service} from "@/model/Service";
import {Report} from "@/model/Report"
import {Order} from "@/model/Order";

export interface Request {
    service?: Service
    order?: Order
    order_id?: string
    user_service_id?: string
    status?: string
    memo?: string
    department?: string
    ward?: string
    physician?: string
    sample?: Sample
    reports?: Report[]
    create_at?: Date
    cart_at?: Date
    specified_at?: Date
    complete_at?: Date
    resample_at?: Date
    reported_at?: Date
    courier_company?: string
    awb_number?: string
}