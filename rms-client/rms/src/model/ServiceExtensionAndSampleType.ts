export interface ServiceExtensionAndSampleType {
    id: string,
    name: string,
    extensions?: Extensions[],
    sample_types?: SampleTypes[]
}

export interface Extensions {
    id: string,
    name: string,
    required: boolean,
    regex: string,
    type: string
}

interface SampleTypes {
    id: string,
    name: string,
}