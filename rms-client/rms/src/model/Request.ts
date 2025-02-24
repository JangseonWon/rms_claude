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
    confirmed_at?: Date,
    lims_received_at?: Date,
    lims_resample_at?: Date,
    lims_completed_at?: Date,
    lims_resample_reason?: string,
    courier_company?: string
    awb_number?: string
    report?: Report
    reports?: Report[]
    user?: User
    request_relation?: RequestRelation
    request_group?: RequestGroup
}