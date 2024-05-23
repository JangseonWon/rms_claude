import {Page} from "@/model/Page";

export async function fetchOrganization(userId: string | undefined, page: Page) {
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
            size: page.size,
            page: page.number
        }),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return res;
}