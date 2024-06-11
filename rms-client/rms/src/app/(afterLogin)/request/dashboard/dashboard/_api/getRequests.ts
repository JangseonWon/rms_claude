import {Search} from "@/model/Search";

export async function getRequests(search: Search) {
    const res = await fetch(`/w-api/dashboard-service/requests`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return  res
}