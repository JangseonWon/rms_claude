export async function getInstitutions(userId: string) {
    const res = await fetch(`/w-api/management-service/users/${userId}/organizations`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}