export async function deleteSampleType(serviceId: string | undefined, sampleTypeId: string) {
    return await fetch(`/w-api/management-service/services/${serviceId}/sample-types/${sampleTypeId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}