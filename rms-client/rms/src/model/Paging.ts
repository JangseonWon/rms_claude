export interface Paging {
    filters: Filter[]
    sort_by?: string
    asc?: boolean
    page: number
    size: number
}
interface Filter {
    key?: string
    value?: string
    operator?: string
    logicalOperator?: string
}