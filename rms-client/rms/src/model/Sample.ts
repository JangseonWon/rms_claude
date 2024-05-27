import {Patient} from "@/model/Patient";
import {SampleType} from "@/model/SampleType";
import {Extension} from "@/model/Extension";

export interface Sample {
    id?: string
    user_sample_id?: string
    age?: number
    sampling_on?: Date
    sample_type?: SampleType
    quantity?: number
    barcode?: string
    patient?: Patient
    extensions?: Extension[]
}