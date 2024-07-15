export interface ServiceExtensionAndSampleType {
    id: string,
    name: string,
    extensions?: Extensions[],
    sample_types?: SampleTypes[]
}

interface Extensions {
    id: string,
    name: string,
    required: boolean,
    regex: string
}

interface SampleTypes {
    id: string,
    name: string,
}