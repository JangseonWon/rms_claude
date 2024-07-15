import {Filter} from "@/model/Filter";

export interface Paging {
    filters: Filter[]
    sort_by?: string
    asc?: boolean
    page: number
    size: number
    filter?: Filter
}

