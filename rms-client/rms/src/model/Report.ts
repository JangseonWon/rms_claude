export interface Report {
    id?: string,
    type?: string,
    value?: string,
    create_at?: Date,
    reported_at?: Date,
    is_latest?: Boolean,
}