export async function putSampleType(sampleType: { sample_type_id: any; service_id: string | undefined }) {
    const res = await fetch(`/w-api/management-service/services/${sampleType.service_id}/sample-types/${sampleType.sample_type_id}`, {
        method: 'PUT',
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