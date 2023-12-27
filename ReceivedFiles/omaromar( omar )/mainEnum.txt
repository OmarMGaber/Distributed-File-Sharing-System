#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>

using namespace std;

/*
 * This class is used to detect the file type of an image based on its header.
 * It supports the following file types:
 * - JPEG
 * - TIFF
 * - RAW
 * - PNG
 * - GIF
 * - BMP
 *
 * else it returns "Unknown"
 */
class ImageExtensionDetector {
public:
    enum FileType {
        JPEG, TIFF, RAW, PNG, GIF, BMP, UNKNOWN
    };

    static string getFileTypeString(FileType fileType) {
        switch (fileType) {
            case JPEG:
                return "JPEG";
            case TIFF:
                return "TIFF";
            case RAW:
                return "RAW";
            case PNG:
                return "PNG";
            case GIF:
                return "GIF";
            case BMP:
                return "BMP";
            case UNKNOWN:
                return "Unknown";
        }
    }

    static FileType getFileType(const string &filepath) {
        auto data = readFile(filepath);
        return getFileType(data);
    }

private:
    static bool isJPEG(const vector<unsigned char> &data) {
        if (data.size() < 2) {
            return false;
        }

        return (data[0] == 0xFF && data[1] == 0xD8);
    }

    static bool isTIFF(const vector<unsigned char> &data) {
        if (data.size() < 4) {
            return false;
        }

        return (data[0] == 0x49 && data[1] == 0x49 && data[2] == 0x2A && data[3] == 0x00);
    }

    static bool isRAW(const vector<unsigned char> &data) {
        if (data.size() < 4) {
            return false;
        }

        return (data[0] == 0xFF && data[1] == 0xD8 && data[2] == 0xFF && data[3] == 0xE1);
    }

    static bool isPNG(const vector<unsigned char> &data) {
        if (data.size() < 8) {
            return false;
        }

        return (data[0] == 0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47 &&
                data[4] == 0x0D && data[5] == 0x0A && data[6] == 0x1A && data[7] == 0x0A);
    }

    static bool isGIF(const vector<unsigned char> &data) {
        if (data.size() < 6) {
            return false;
        }

        return (data[0] == 0x47 && data[1] == 0x49 && data[2] == 0x46 && data[3] == 0x38 &&
                data[4] == 0x39 || data[4] == 0x37 && data[5] == 0x61);
    }

    static bool isBMP(const vector<unsigned char> &data) {
        if (data.size() < 2) {
            return false;
        }

        return (data[0] == 0x42 && data[1] == 0x4D);
    }

    static vector<unsigned char> readFile(const string &filepath) {
        ifstream file(filepath, ios::binary);

        if (!file.is_open())
            throw runtime_error("Error opening file: " + filepath);

        vector<unsigned char> data;

        try {
            file.seekg(0, ios::end);
            size_t size = file.tellg();

            data.resize(size);
            file.seekg(0, ios::beg);
            file.read((char *) &data[0], size);

            file.close();
        } catch (exception &e) {
            file.close();
            throw runtime_error("Error reading file: " + filepath);
        }

        return data;
    }

    static FileType getFileType(const vector<unsigned char> &data) {
        if (isJPEG(data))
            return FileType::JPEG;

        else if (isTIFF(data))
            return FileType::TIFF;

        else if (isRAW(data))
            return FileType::RAW;

        else if (isPNG(data))
            return FileType::PNG;

        else if (isGIF(data))
            return FileType::GIF;

        else if (isBMP(data))
            return FileType::BMP;

        return FileType::UNKNOWN;
    }
};

int main() {

    string filepath = R"(E:\Projects\Cpp\ImageExtensionDetector\test\testingFiles\tiff.tiff)";
    ImageExtensionDetector::FileType fileType = ImageExtensionDetector::getFileType(filepath);
    cout << "The file type is: " << ImageExtensionDetector::getFileTypeString(fileType) << '\n';

    return 0;
}
