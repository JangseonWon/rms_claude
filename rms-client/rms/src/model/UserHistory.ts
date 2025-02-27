export interface UserHistory {
    id: string
    changed_at?: Date
    changed_by?: string
    field_name?: string
    new_value?: string
    old_value?: string
    user_id?: string
}