export async function getSampleType(sampleTypeId: string) {
    return await fetch(`/w-api/management-service/sample-types/${sampleTypeId}`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include',
        cache: 'no-store'
    });
}