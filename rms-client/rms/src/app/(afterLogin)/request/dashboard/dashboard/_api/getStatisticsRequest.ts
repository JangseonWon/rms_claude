import _ from 'lodash'
import {Statistics} from "@/model/Statistics";

export async function getStatisticsRequest() {
    const res = await fetch(`/w-api/dashboard-service/statistics`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return _.mapKeys(await res.json(), (v, k) => _.camelCase(k)) as Statistics
}