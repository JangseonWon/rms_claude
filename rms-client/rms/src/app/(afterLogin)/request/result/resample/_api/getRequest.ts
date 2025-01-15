export async function getRequest(sampleId: string, serviceId: string) {
    return await fetch(`/w-api/result-service/requests?sample_id=${sampleId}&service_id=${serviceId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}