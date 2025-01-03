import {SampleType} from "@/model/SampleType";

export async function patchSampleType(sampleType: SampleType) {
    return await fetch(`/w-api/management-service/sample-types/${sampleType.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(sampleType),
        credentials: 'include',
        cache: 'no-store'
    });
}