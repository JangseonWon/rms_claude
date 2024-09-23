import {Categories} from "@/model/Categories";
import {SampleType} from "@/model/SampleType";
import {Extension} from "@/model/Extension";

export interface Service {
    id?: string,
    name?: string
    category_id?: string,
    category?: Categories,
    sample_types?: SampleType[],
    extensions?: Extension[]

}