export interface Search {
    page: Page
    filters?: Filter[]
}
interface Page {
    number: number
    size: number
}
interface Filter {
    field: string
    value?: string
    operator?: string
    logicalOperator?: string
    filters?: Filter[]
}