export async function postSampleType(sampleType: { sample_type_id: any; service_id: string | undefined }) {
    return await fetch(`/w-api/management-service/services/${sampleType.service_id}/sample-types/${sampleType.sample_type_id}`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(sampleType),
        credentials: 'include',
        cache: 'no-store'
    });
}