export interface Extension {
    id?: string
    value?: string
    name?: string
    required?: boolean
    regex?: string
    type?: ExtensionType
}

export enum ExtensionType {
    BOOLEAN = "BOOLEAN",
    STRING = "STRING",
    INTEGER = "INTEGER",
    NUMBER = "NUMBER",
    FLOAT = "FLOAT",
    TEXT = "TEXT",
    LIST = "LIST"
}