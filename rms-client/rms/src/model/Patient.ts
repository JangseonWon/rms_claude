import {Organization} from "@/model/Organization";

export interface Patient {
    name: string,
    serial: string,
    sex: string
    organization: Organization,
    birth_year: number,
    birth_month: number,
    birth_day: number

}