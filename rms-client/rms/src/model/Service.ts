import {Categories} from "@/model/Categories";
import {SampleType} from "@/model/SampleType";
import {Extension} from "@/model/Extension";

export interface Service {
    id?: string,
    name?: string,
    name_kr?: string,
    type?: string,
    group_name?: string,
    category_id?: string,
    category?: Categories,
    sample_types?: SampleType[],
    extensions?: Extension[]

}