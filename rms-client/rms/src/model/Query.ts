import {Filter} from "@/model/Filter";

export interface Query {
    sorts?: Sort[]
    sort_by?: string // 제거예정
    asc?: boolean
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