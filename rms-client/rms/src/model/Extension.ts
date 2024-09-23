export interface Extension {
    id?: string
    name?: string
    required?: boolean
    regex?: string
    type?: ExtensionType
}

export enum ExtensionType {
    BOOLEAN = "BOOLEAN",
    STRING = "STRING",
    INTEGER = "INTEGER",
    FLOAT = "FLOAT",
    TEXT = "TEXT",
    LIST = "LIST"
}