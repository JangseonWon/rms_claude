import {Page} from "@/model/Page";

export async function fetchResampleOrder(userId: string | undefined, page: Page) {
    const res = await fetch(`/w-api/order-service/requests?status=confirm`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            filter: [{
            }],
            sort_by: "last_modify_at",
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