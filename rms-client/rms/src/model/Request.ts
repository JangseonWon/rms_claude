import {Sample} from "@/model/Sample";
import {Service} from "@/model/Service";
import {Report} from "@/model/Report"
import {User} from "@/model/User";
import {RequestRelation} from "@/model/RequestRelation";
import {RequestGroup} from "@/model/RequestGroup";

export interface Request {
    service?: Service
    user_service_id?: string
    status?: string
    memo?: string
    department?: string
    ward?: string
    physician?: string
    sample?: Sample
    create_at?: Date
    cart_at?: Date
    specified_at?: Date
    complete_at?: Date
    resample_at?: Date
    reported_at?: Date
    courier_company?: string
    awb_number?: string
    report?: Report
    reports?: Report[]
    user?: User
    request_relation?: RequestRelation
    request_group?: RequestGroup
}