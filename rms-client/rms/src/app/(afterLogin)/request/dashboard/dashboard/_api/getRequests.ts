import {Page} from "@/model/Page";

export async function getRequests(page: Page) {
    const res = await fetch(`/w-api/dashboard-service/requests`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            page: {
                size: page.size,
                number: page.number
            }
        }),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return  res
}