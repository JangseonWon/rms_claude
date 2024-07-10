import {Order} from "@/model/Order";

export async function fetchAddCartAndOrder(order: Order[]) {
    const res = await fetch(`/w-api/product-service/orders`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(order),
        credentials: 'include',
        cache: 'no-store'
    });
    
    if (!res.ok) throw new Error('Failed to fetch data')
    return  res
}