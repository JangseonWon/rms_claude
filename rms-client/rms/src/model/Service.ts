import {Categories} from "@/model/Categories";
import {SampleType} from "@/model/SampleType";
import {Extension} from "@/model/Extension";

export interface Service {
    id?: string,
    name?: string,
    name_kr?: string,
    category_id?: string,
    category?: Categories,
    sample_types?: SampleType[],
    extensions?: Extension[]

}