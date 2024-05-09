import {Patient} from "@/model/Patient";

export interface Sample {
    id: string,
    quantity: number,
    barcode: string,
    patient: Patient
}