export async function fetchOrganization(userId: string | undefined, pageSize: number, pageNumber: number) {
    const res = await fetch(`/w-api/organization-service/organizations`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            filter: [{
                key: "user_id",
                value: userId
            }],
            sort_by: "user_id",
            asc: false,
            size: pageSize,
            page: pageNumber
        }),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return res;
}