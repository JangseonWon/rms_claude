export interface Extension {
    id?: string
    value?: string
    name?: string
    name_kr?: string
    required?: boolean
    regex?: string
    sort_extension?: number
    type?: ExtensionType
}

export enum ExtensionType {
    BOOLEAN = "BOOLEAN",
    STRING = "STRING",
    INTEGER = "INTEGER",
    FLOAT = "FLOAT",
    TEXT = "TEXT",
    LIST = "LIST",
    PROBAND_SEARCH = "PROBAND_SEARCH",
    PROBAND_LIST = "PROBAND_LIST",
    REGISTRATION_ID = "REGISTRATION_ID"
}