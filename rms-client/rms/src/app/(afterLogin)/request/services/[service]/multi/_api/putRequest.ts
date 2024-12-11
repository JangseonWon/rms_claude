import {Order} from "@/model/Order";

export async function putRequest(order: Order) {
    const res = await fetch(`/w-api/catalog-service/requests`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(order),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}