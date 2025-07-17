import {Filter} from "@/model/Filter";

export interface Query {
    sorts?: Sort[]
    page?: number
    size?: number
    filter_groups?: FilterGroup[]
}

export interface FilterGroup {
    condition_type?: string;  // AND 또는 OR
    filters?: Filter[];
}

export interface Sort {
    table?: string,
    column?: string,
    asc?: boolean
}