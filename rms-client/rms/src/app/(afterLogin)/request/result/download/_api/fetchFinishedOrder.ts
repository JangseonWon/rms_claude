import {Query} from "@/model/Query";

export async function fetchFinishedOrder(search: Query) {
    const res = await fetch(`/w-api/order-service/requests?status=download`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return res;
}