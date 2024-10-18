import {Filter} from "@/model/Filter";

export interface Query {
    sort_by?: string
    asc?: boolean
    page?: number
    size?: number
    filter_groups?: FilterGroup[]
}

export interface FilterGroup {
    condition_type?: string;  // AND 또는 OR
    filters?: Filter[];
}