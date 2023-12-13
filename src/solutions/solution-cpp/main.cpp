#include <iostream>
#include <fstream>
#include <vector>
#include <set>
using namespace std;

struct Song {
    int id;
    string name;
    int streams;
    int c1, c2, c3;
    int playlist;

    bool operator<(const Song& other) const {
        return (streams == other.streams) ? (name < other.name) : (streams > other.streams);
    }

} songs[1000001];

int l, limc1, limc2, limc3;
int m;

vector<vector<int>> playlists;
vector<int> addedCount[3];

struct cmp1 {
    bool operator()(int a, int b) const {
        return songs[a].c1 == songs[b].c1 ? songs[a].name < songs[b].name : songs[a].c1 > songs[b].c1;
    }
};

struct cmp2 {
    bool operator()(int a, int b) const {
        return songs[a].c2 == songs[b].c2 ? songs[a].name < songs[b].name : songs[a].c2 > songs[b].c2;
    }
};

struct cmp3 {
    bool operator()(int a, int b) const {
        return songs[a].c3 == songs[b].c3 ? songs[a].name < songs[b].name : songs[a].c3 > songs[b].c3;
    }
};

struct cmp {
    bool operator()(int a, int b) const {
        return songs[a] < songs[b];
    }
};

std::set<int, cmp1> epicBlendC1;
std::set<int, cmp2> epicBlendC2;
std::set<int, cmp3> epicBlendC3;

vector<vector<int>> addToEpicBlend(int song) {
    vector<vector<int>> ret(3, vector<int>(2, 0));

    epicBlendC1.insert(song);
    if (epicBlendC1.size() > limc1) {
        if (song != *epicBlendC1.begin()) {
            ret[0][0] = song;
            ret[0][1] = *epicBlendC1.begin();
        }
        epicBlendC1.erase(epicBlendC1.begin());
    }
    
    if (epicBlendC2.size() > limc2) {
        if (song != *epicBlendC2.begin()) {
            ret[1][0] = song;
            ret[1][1] = *epicBlendC2.begin();
        }
        epicBlendC2.erase(epicBlendC2.begin());
    }

    if (epicBlendC3.size() > limc3) {
        if (song != *epicBlendC3.begin()) {
            ret[2][0] = song;
            ret[2][1] = *epicBlendC3.begin();
        }
        epicBlendC3.erase(epicBlendC3.begin());
    }

    return ret;

}



set<int, cmp> generateEpicBlend1() {

    set<int, cmp> epicBlend;

    int added = 0;
    vector<int> addedCount = vector<int>(m+1, 0);
    
    for (auto s : epicBlendC1) {
        if (addedCount[songs[s].playlist] >= l) continue;
        epicBlend.insert(s);
        addedCount[songs[s].playlist]++;
        added++;

        if (added == limc1) break;
    }

    return epicBlend;
    
}

set<int, cmp> generateEpicBlend2() {

    set<int, cmp> epicBlend;

    int added = 0;
    vector<int> addedCount = vector<int>(m+1, 0);
    
    for (auto s : epicBlendC2) {
        if (addedCount[songs[s].playlist] >= l) continue;
        epicBlend.insert(s);
        addedCount[songs[s].playlist]++;
        added++;

        if (added == limc2) break;
    }

    return epicBlend;
    
}

set<int, cmp> generateEpicBlend3() {

    set<int, cmp> epicBlend;

    int added = 0;
    vector<int> addedCount = vector<int>(m+1, 0);
    
    for (auto s : epicBlendC3) {
        if (addedCount[songs[s].playlist] >= l) continue;
        epicBlend.insert(s);
        addedCount[songs[s].playlist]++;
        added++;

        if (added == limc3) break;
    }

    return epicBlend;
    
}

int main(int argc, char const *argv[])
{
    // g++ -std=c++17 .\solutions\naive.cpp -o .\solutions\naive.exe
    // .\solutions\naive.exe .\inputs\songs.txt .\inputs\testcase00.txt .\solutions\outputs\output00.txt
    // .\solutions\naive.exe .\sample\songs.txt .\sample\inputs\0.txt .\solutions\outputs\sample_0.txt
    // .\solutions\naive.exe .\sample\songs.txt .\sample\inputs\1.txt .\solutions\outputs\sample_1.txt
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    cerr << "Starting" << endl;
    if (argc != 4) {
        cout << "Usage: " << argv[0] << " <songs> <playlists> <output>" << endl;
        return 1;   
    }
    ifstream inputsongs(argv[1]);
    ofstream output(argv[3]);

    int n;
    inputsongs >> n;

    for (int i = 1; i <= n; i++) {
        inputsongs >> songs[i].id >> songs[i].name >> songs[i].streams >> songs[i].c1 >> songs[i].c2 >> songs[i].c3;
    }

    inputsongs.close();

    cerr << "Songs loaded" << endl;

    ifstream input(argv[2]);
    
    input >> l >> limc1 >> limc2 >> limc3 >> m;
    
    playlists.resize(m+1);
    addedCount[0].resize(m+1, 0);
    addedCount[1].resize(m+1, 0);
    addedCount[2].resize(m+1, 0);

    for (int i = 1; i <= m; i++) {
        int id, k;
        input >> id >> k;
        playlists[i].resize(k);
        for (int j = 0; j < k; j++) {
            input >> playlists[i][j];
            songs[playlists[i][j]].playlist = i;
            epicBlendC1.insert(playlists[i][j]);
            epicBlendC2.insert(playlists[i][j]);
            epicBlendC3.insert(playlists[i][j]);
        }

    }

    cerr << "Playlists loaded" << endl;

    set<int, cmp> epicBlend1 = generateEpicBlend1();
    set<int, cmp> epicBlend2 = generateEpicBlend2();
    set<int, cmp> epicBlend3 = generateEpicBlend3();

    auto diff = [&](set<int, cmp>& newEpicBlend, set<int, cmp>& oldEpicBlend) {
        pair<int, int> ret = {0, 0};
        for (auto s : newEpicBlend) {
            if (oldEpicBlend.find(s) == oldEpicBlend.end()) {
                ret.first = s;
                break;
            }
        }

        for (auto s : oldEpicBlend) {
            if (newEpicBlend.find(s) == newEpicBlend.end()) {
                ret.second = s;
                break;
            }
        }

        return ret;

    }; 

    int q;
    input >> q;

    while (q--) {
        string type;
        input >> type;

        if (q % 1000 == 0) cerr << "Remaining: " << q << endl;

        if (type == "ADD") {
            int song, playlist;
            input >> song >> playlist;
            songs[song].playlist = playlist;
            epicBlendC1.insert(song);
            epicBlendC2.insert(song);
            epicBlendC3.insert(song);

            set<int, cmp> newEpicBlend1 = generateEpicBlend1();
            set<int, cmp> newEpicBlend2 = generateEpicBlend2();
            set<int, cmp> newEpicBlend3 = generateEpicBlend3();

            pair<int, int> diff1 = diff(newEpicBlend1, epicBlend1);
            pair<int, int> diff2 = diff(newEpicBlend2, epicBlend2);
            pair<int, int> diff3 = diff(newEpicBlend3, epicBlend3);

            epicBlend1 = newEpicBlend1;
            epicBlend2 = newEpicBlend2;
            epicBlend3 = newEpicBlend3;

            output << diff1.first << " " << diff2.first << " " << diff3.first << "\n" << diff1.second << " " << diff2.second << " " << diff3.second << endl;


        } else if (type == "REM") {
            int song, playlist;
            input >> song >> playlist;
            songs[song].playlist = 0;
            epicBlendC1.erase(song);
            epicBlendC2.erase(song);
            epicBlendC3.erase(song);

            set<int, cmp> newEpicBlend1 = generateEpicBlend1();
            set<int, cmp> newEpicBlend2 = generateEpicBlend2();
            set<int, cmp> newEpicBlend3 = generateEpicBlend3();

            pair<int, int> diff1 = diff(newEpicBlend1, epicBlend1);
            pair<int, int> diff2 = diff(newEpicBlend2, epicBlend2);
            pair<int, int> diff3 = diff(newEpicBlend3, epicBlend3);

            epicBlend1 = newEpicBlend1;
            epicBlend2 = newEpicBlend2;
            epicBlend3 = newEpicBlend3;

            output << diff1.first << " " << diff2.first << " " << diff3.first << "\n" << diff1.second << " " << diff2.second << " " << diff3.second << endl;

        } else if (type == "ASK") {
            set<int, cmp> epicBlend;
            epicBlend.insert(epicBlend1.begin(), epicBlend1.end());
            epicBlend.insert(epicBlend2.begin(), epicBlend2.end());
            epicBlend.insert(epicBlend3.begin(), epicBlend3.end());

            for (auto s : epicBlend) {
                output << s;
                if (s != *epicBlend.rbegin()) output << " ";
            }
            output << endl;
        }
        
    }

    input.close();
    output.close();

    
}