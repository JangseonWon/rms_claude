export async function postSampleType() {
    return await fetch(`/w-api/management-service/alis/services`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include',
        cache: 'no-store'
    });
}