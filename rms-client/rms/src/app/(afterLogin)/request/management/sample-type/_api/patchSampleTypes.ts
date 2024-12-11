import {SampleType} from "@/model/SampleType";

export async function patchSampleType(sampleType: SampleType) {
    const res = await fetch(`/w-api/management-service/sample-types/${sampleType.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(sampleType),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}