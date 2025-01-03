export async function getSampleTypes() {
    return await fetch(`/w-api/management-service/sample-types`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}