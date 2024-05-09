import {Sample} from "@/model/Sample";
import {Service} from "@/model/Service";

export interface Request {
    order_id: string,
    service_id: string,
    status: string,
    sample: Sample,
    service: Service,
    create_at: Date
}